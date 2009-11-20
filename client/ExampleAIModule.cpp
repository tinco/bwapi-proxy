/**
 * AIModule implementation for communicating with a remote java process (ProxyBot).
 *
 * Uses the winsock library for sockets, include "wsock32.lib" in the linker inputs
 *
 * Note: this implementation uses a blocking socket. On each frame, an update message is
 *       sent to the ProxyBot, then the socket waits for a command message from the 
 *		 ProxyBot. The process blocks while waiting for a response, so the ProxyBot
 *		 should immediately respond to updates.
 *
 * TODO: modify core BWAPI to assign IDs to units and provide a way of retrieving
 *       units by their ID 
 */
#include "ExampleAIModule.h"
using namespace BWAPI;

#include <winsock.h>
#include <stdio.h>
#include <sstream>
#include <string>

/** port to connect to on the java side */
#define PORTNUM 12345

/** size of recv buffer for commands from the Proxy bot */
#define recvBufferSize 4096
char recieveBuffer[recvBufferSize];

/** buffer data buffer */
#define sendBufferSize 100000
char sendBuffer[sendBufferSize];

/** pixels per tile in starcraft */
#define pixelsPerTile 32

/** max indexes of static type data */
#define maxUnitTypes 230
#define maxTechTypes 47
#define maxUpgradeTypes 63

/** mapping of IDs to types */
UnitType unitTypeMap[maxUnitTypes];
TechType techTypeMap[maxTechTypes];
UpgradeType upgradeTypeMap[maxUpgradeTypes];

/** export information about static type data? */
bool exportTypeData = false;

/** display commands made by the bot?, recieved from ProxyBot */
bool logCommands = false;

/** mapping of unit objects to a unique ID, sent is sent to the java process */
std::map<Unit*, int> unitMap;
std::map<int, Unit*> unitIDMap;

/** used to assign unit object IDs */
int unitIDCounter = 1;

/** used by the append method */
int digits[9];

/** socket identifier */
int proxyBotSocket = -1;

/** functions */
int initSocket();
void exportStaticData();
std::string toString(int value);
std::string toString(bool value);
void append(FILE *log, std::string data);
void handleCommand(int command, int unitID, int arg0, int arg1, int arg2);
BWAPI::UnitType getUnitType(int type); 
BWAPI::TechType getTechType(int type);
BWAPI::UpgradeType getUpgradeType(int type);
void loadTypeMaps();
BWAPI::TilePosition getTilePosition(int x, int y);
BWAPI::Position getPosition(int x, int y);
BWAPI::Unit* getUnit(int unitID);
int append(int val, char* buf, int currentIndex);

/**
 * Called at the start of a match. 
 */
void ExampleAIModule::onStart()
{
	loadTypeMaps();

	// export type data?
	if (exportTypeData) {
		exportTypeData = false;
		exportStaticData();
	}

	// First, check if we are already connected to the Java proxy
	if (proxyBotSocket == -1) {
		Broodwar->sendText("Connecting to ProxyBot");		
		proxyBotSocket = initSocket();

		// connected failed
		if (proxyBotSocket == -1) {
			Broodwar->sendText("ProxyBot connected failed");
			return;
		}
		else {
			Broodwar->sendText("Sucessfully connected to ProxyBot");
		}
	}
	else {
		Broodwar->sendText("Already connected to ProxyBot");
	}

	// 1. initiate communication with the proxy bot
	std::string ack("NewGame");
	ack += ";" + toString(Broodwar->self()->getID());

	std::set<Player*> players = Broodwar->getPlayers();
	for(std::set<Player*>::iterator i=players.begin();i!=players.end();i++) {
		int id = (*i)->getID();
		std::string race = (*i)->getRace().getName();
		std::string name = (*i)->getName();
		int type = (*i)->playerType().getID();
		bool ally = Broodwar->self()->isAlly(*i);

		ack += ":" + toString(id)
			 + ";" + race
			 + ";" + name 
			 + ";" + toString(type)
			 + ";" + (ally ? "1" : "0");
	}

	ack += "\n";
	send(proxyBotSocket,(char*)ack.c_str(), ack.size(), 0);

	// 2. Wait for bot options
	char buf[1024];
	int numBytes = recv(proxyBotSocket , buf , 1024 , 0);	
	if (buf[0] == '1') Broodwar->enableFlag(Flag::UserInput);
	if (buf[1] == '1') Broodwar->enableFlag(Flag::CompleteMapInformation); // Note: Fog of War remains
	logCommands = (buf[2] == '1');
	bool terrainAnalysis = (buf[3] == '1');

	// 3. send starting locations
	std::string locations("Locations");
	std::set<TilePosition> startSpots = Broodwar->getStartLocations();
	for(std::set<TilePosition>::iterator i=startSpots.begin();i!=startSpots.end();i++)
	{
		locations += ":" + toString(i->x())
				   + ";" + toString(i->y());
	}

	locations += "\n";
    char *slBuf = (char*)locations.c_str();
    send(proxyBotSocket, slBuf, locations.size(), 0);

	// 4. send the map data
	std::string mapName = Broodwar->mapName();
	int mapWidth = Broodwar->mapWidth();
	int mapHeight = Broodwar->mapHeight();

	std::string mapData(mapName);
	mapData += ":" + toString(mapWidth)
  	 		 + ":" + toString(mapHeight)
			 + ":";

	for (int y=0; y<mapHeight; y++) {	
		for (int x=0; x<mapWidth; x++) {
			mapData += toString(Broodwar->groundHeight(4*x, 4*y));
			mapData += (Broodwar->buildable(x, y)) ? "1" : "0";
			mapData += (Broodwar->walkable(4*x, 4*y)) ? "1" : "0";
		}
	}

	mapData += "\n";
	char *sbuf = (char*)mapData.c_str();
	send(proxyBotSocket, sbuf, mapData.size(), 0);

	// 5. Send chokepoint data
	if (terrainAnalysis) {
		BWTA::analyze();

		std::string chokes("Chokes");
		std::set<BWTA::Chokepoint*> chokepoints = BWTA::getChokepoints();
		for (std::set<BWTA::Chokepoint*>::iterator i=chokepoints.begin();i!=chokepoints.end();i++)
		{
			chokes += ":" + toString((*i)->getCenter().x())
					+ ";" + toString((*i)->getCenter().y())
					+ ";" + toString((int)(*i)->getWidth());
		}

		chokes += "\n";
		char *scbuf = (char*)chokes.c_str();
		send(proxyBotSocket, scbuf, chokes.size(), 0);

		// 6. send base location data
		std::string bases("Bases");
		std::set<BWTA::BaseLocation*> baseLocation =  BWTA::getBaseLocations();
		for (std::set<BWTA::BaseLocation*>::iterator i=baseLocation.begin();i!=baseLocation.end();i++)
		{
			bases += ":" + toString((*i)->getTilePosition().x())
				   + ";" + toString((*i)->getTilePosition().y());
		}

		bases += "\n";
		char *sbbuf = (char*)bases.c_str();
		send(proxyBotSocket, sbbuf, bases.size(), 0);
	}
}

/**
 * Runs every frame 
 *
 * Sends the unit status to the ProxyBot, then waits for a list of command messages.
 */
void ExampleAIModule::onFrame()
{
	// check if the Proxy Bot is connected
	if (proxyBotSocket == -1) {
		return;
	}	

	// assign IDs to the units
	std::set<Unit*> units = Broodwar->getAllUnits();	
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		int unitID = unitMap[*i];

		if (unitID == 0) {
			unitID = unitIDCounter++; 
			unitMap[*i] = unitID;
			unitIDMap[unitID] = *i;
		}
	}

    // 1. send the unit status's to the Proxy Bot
	sendBuffer[0] = 's';
	int index = 1;
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->minerals(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->gas(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->supplyUsed(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->supplyTotal(), sendBuffer, index);

	// get the research status
	int research[47];
	for (int i=0; i<47; i++) research[i] = 0;

	std::set<TechType> tektypes = TechTypes::allTechTypes();
	for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++) {
		if (Broodwar->self()->researched((*i))) {
			research[(*i).getID()] = 4;
		}
		else if (Broodwar->self()->researching((*i))) {
			research[(*i).getID()] = 1;
		}
		else {
			research[(*i).getID()] = 0;
		}
	} 

	sendBuffer[index++] = ';';
	for (int i=0; i<47; i++) {
		index = append(research[i], sendBuffer, index);
	}

	// get the upgrade status
	int ups[63];
	for (int i=0; i<63; i++) ups[i] = 0;

	std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
	for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++) {
		if (Broodwar->self()->upgrading((*i))) {
			ups[(*i).getID()] = 4;
		}
		else {
			ups[(*i).getID()] = Broodwar->self()->upgradeLevel((*i));
		}
	}

	sendBuffer[index++] = ';';
	for (int i=0; i<63; i++) {
		index = append(ups[i], sendBuffer, index);
	}

	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++)
	{
		int unitID = unitMap[*i];

		sendBuffer[index++] = ':';
		index = append(unitID, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getPlayer()->getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getType().getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
//		index = append((*i)->getPosition().x()/32, sendBuffer, index);
		index = append((*i)->getTilePosition().x(), sendBuffer, index);
		sendBuffer[index++] = ';';
//		index = append((*i)->getPosition().y()/32, sendBuffer, index);
		index = append((*i)->getTilePosition().y(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getHitPoints()/256, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getShields()/256, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getEnergy()/256, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingBuildTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingTrainTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingResearchTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingUpgradeTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getOrderTimer(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getOrder().getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getResources(), sendBuffer, index);
		sendBuffer[index++] = ';';
		Unit *addon = (*i)->getAddon();	// add on ID
		int addonID = 0;
		if (addon != NULL) addonID = unitMap[addon];
		index = append(addonID, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getSpiderMineCount(), sendBuffer, index);
	} 

	sendBuffer[index++] = '\n';
	send(proxyBotSocket, sendBuffer, index, 0);

	// 2. process commands
	int numBytes = recv(proxyBotSocket , recieveBuffer, recvBufferSize, 0);

	char *message = new char[numBytes + 1];
	message[numBytes] = 0;
	for (int i=0; i<numBytes; i++) 
	{ 
		message[i] = recieveBuffer[i];
	}

	// tokenize the commands
	char* token = strtok(message, ":");
	token = strtok(NULL, ":");			// eat the command part of the message
    int commandCount = 0;
    char* commands[100];

	while (token != NULL) 
	{
		commands[commandCount] = token;
		commandCount++;
		token = strtok(NULL, ":");
	}

	// tokenize the arguments
	for (int i=0; i<commandCount; i++) 
	{
		char* command = strtok(commands[i], ";");
		char* unitID = strtok(NULL, ";");
		char* arg0 = strtok(NULL, ";");
		char* arg1 = strtok(NULL, ";");
		char* arg2 = strtok(NULL, ";");

		handleCommand(atoi(command), atoi(unitID), atoi(arg0), atoi(arg1), atoi(arg2));
	}
}

/** 
 * Append a number to the char array.
 */
int append(int val, char* buf, int currentIndex) 
{

	if (val <= 0) {
		buf[currentIndex++] = '0';
		return currentIndex;
	}

	for (int i=0; i<9; i++) {
		digits[i] = val%10;

		if (val >= 10) {
			val /= 10;
		}
		else {
			for (int j=i; j>=0; j--) {
				buf[currentIndex++] = ('0' + digits[j]);
			}

			break;
		}
	}

	return currentIndex;
}

/**
 * Executes the specified command with the given arguments. Does limited sanity checking.
 *
 * The command value is specified by the StarCraftCommand enumeration in Command.java.
 */
void handleCommand(int command, int unitID, int arg0, int arg1, int arg2)
{
	if (command == 41) {
		Broodwar->sendText("Set game speed: %d", unitID);
		Broodwar->setLocalSpeed(unitID);
		return;
	}

	// check that the unit ID is valid
	Unit* unit = unitIDMap[unitID];
	if (unit == NULL) {
		Broodwar->sendText("Issued command to invalid unit ID: %d", unitID);
		return;
	}

	// execute the command
	switch (command) {

	    // virtual bool attackMove(Position position) = 0;
		case 1:
			if (logCommands) Broodwar->sendText("Unit:%d attackMove(%d, %d)",unitID, arg0, arg1);
			unit->attackMove(getPosition(arg0, arg1));
			break;
		// virtual bool attackUnit(Unit* target) = 0;
		case 2:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d attackUnit(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d attackUnit(%d)", unitID, arg0);
				unit->attackUnit(getUnit(arg0));
			}
			break;
		// virtual bool rightClick(Position position) = 0;
		case 3:
			if (logCommands) Broodwar->sendText("Unit:%d rightClick(%d, %d)",unitID, arg0, arg1);
			unit->rightClick(getPosition(arg0, arg1));
			break;
		// virtual bool rightClick(Unit* target) = 0;
		case 4:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d rightClick(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d rightClick(%d)", unitID, arg0);
				unit->rightClick(getUnit(arg0));
			}
			break;
		// virtual bool train(UnitType type) = 0;
		case 5:
			if (getUnitType(arg0) < 0) { // NULL doesnt work here, NULL = 0, Terran_Marine=0
				Broodwar->sendText("Invalid Command, Unit:%d train(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d train(%d)", unitID, arg0);
				unit->train(getUnitType(arg0));
			}
			break;
		// virtual bool build(TilePosition position, UnitType type) = 0;
		case 6:
//			Broodwar->drawBox(CoordinateType::Map, 32*arg0, 32*arg1, 32*arg0 + 96, 32*arg1 + 64, Colors::Yellow, true);
			if (getUnitType(arg2) == NULL) {
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d build(%d, %d, %d)", unitID, arg0, arg1, arg2);
				unit->build(getTilePosition(arg0, arg1), getUnitType(arg2));
			}
			break;
		// virtual bool buildAddon(UnitType type) = 0;
		case 7:
			if (getUnitType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d buildAddon(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d buildAddon(%d)", unitID, arg0);
				unit->buildAddon(getUnitType(arg0));
			}
			break;
		// virtual bool research(TechType tech) = 0;
		case 8:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d research(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d research(%d)", unitID, arg0);
				unit->research(getTechType(arg0));
			}
			break;
		// virtual bool upgrade(UpgradeType upgrade) = 0;
		case 9:
			if (getUpgradeType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d upgrade(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d upgrade(%d)", unitID, arg0);
				unit->upgrade(getUpgradeType(arg0));
			}
			break;
		// virtual bool stop() = 0;
		case 10:
			if (logCommands) Broodwar->sendText("Unit:%d stop()", unitID);
			unit->stop();
			break;
		// virtual bool holdPosition() = 0;
		case 11:
			if (logCommands) Broodwar->sendText("Unit:%d holdPosition()", unitID);
			unit->holdPosition();
			break;
		// virtual bool patrol(Position position) = 0;
		case 12:
			if (logCommands) Broodwar->sendText("Unit:%d patrol(%d, %d)", unitID, arg0, arg1);
			unit->patrol(getPosition(arg0, arg1));
			break;
		// virtual bool follow(Unit* target) = 0;
		case 13:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d follow(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d follow(%d)", unitID, arg0);
				unit->follow(getUnit(arg0));
			}
			break;
		// virtual bool setRallyPosition(Position target) = 0;
		case 14:
			if (logCommands) Broodwar->sendText("Unit:%d setRallyPosition(%d, %d)", unitID, arg0, arg1);
			unit->setRallyPosition(getPosition(arg0, arg1));
			break;
		// virtual bool setRallyUnit(Unit* target) = 0;
		case 15:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d setRallyUnit(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d setRallyUnit(%d)", unitID, arg0);
				unit->setRallyUnit(getUnit(arg0));
			}
			break;
		// virtual bool repair(Unit* target) = 0;
		case 16:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d repair(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d repair(%d)", unitID, arg0);
				unit->repair(getUnit(arg0));
			}
			break;
		// virtual bool morph(UnitType type) = 0;
		case 17:
			if (getUnitType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d morph(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d morph(%d)", unitID, arg0);
				unit->morph(getUnitType(arg0));
			}
			break;
		// virtual bool burrow() = 0;
		case 18:
			if (logCommands) Broodwar->sendText("Unit:%d burrow()", unitID);
			unit->burrow();
			break;
		// virtual bool unburrow() = 0;
		case 19:
			if (logCommands) Broodwar->sendText("Unit:%d unburrow()", unitID);
			unit->unburrow();
			break;
		// virtual bool siege() = 0;
		case 20:
			if (logCommands) Broodwar->sendText("Unit:%d siege()", unitID);
			unit->siege();
			break;
		// virtual bool unsiege() = 0;
		case 21:
			if (logCommands) Broodwar->sendText("Unit:%d unsiege()", unitID);
			unit->unsiege();
			break;
		// virtual bool cloak() = 0;
		case 22:
			if (logCommands) Broodwar->sendText("Unit:%d cloak()", unitID);
			unit->cloak();
			break;
		// virtual bool decloak() = 0;
		case 23:
			if (logCommands) Broodwar->sendText("Unit:%d decloak()", unitID);
			unit->decloak();
			break;
		// virtual bool lift() = 0;
		case 24:
			if (logCommands) Broodwar->sendText("Unit:%d lift()", unitID);
			unit->lift();
			break;
		// virtual bool land(TilePosition position) = 0;
		case 25:
			if (logCommands) Broodwar->sendText("Unit:%d land(%d, %d)", unitID, arg0, arg1);
			unit->land(getTilePosition(arg0, arg1));
			break;
		// virtual bool load(Unit* target) = 0;
		case 26:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d load(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d load(%d)", unitID, arg0);
				unit->load(getUnit(arg0));
			}
			break;
		// virtual bool unload(Unit* target) = 0;
		case 27:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d unload(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d unload(%d)", unitID, arg0);
				unit->unload(getUnit(arg0));
			}
			break;
		// virtual bool unloadAll() = 0;
		case 28:
			if (logCommands) Broodwar->sendText("Unit:%d unloadAll()", unitID);
			unit->unloadAll();
			break;
		// virtual bool unloadAll(Position position) = 0;
		case 29:
			if (logCommands) Broodwar->sendText("Unit:%d unloadAll(%d, %d)", unitID, arg0, arg1);
			unit->unloadAll(getPosition(arg0, arg1));
			break;
		// virtual bool cancelConstruction() = 0;
		case 30:
			if (logCommands) Broodwar->sendText("Unit:%d cancelConstruction()", unitID);
			unit->cancelConstruction();
			break;
		// virtual bool haltConstruction() = 0;
		case 31:
			if (logCommands) Broodwar->sendText("Unit:%d haltConstruction()", unitID);
			unit->haltConstruction();
			break;
		// virtual bool cancelMorph() = 0;
		case 32:
			if (logCommands) Broodwar->sendText("Unit:%d cancelMorph()", unitID);
			unit->cancelMorph();
			break;
		// virtual bool cancelTrain() = 0;
		case 33:
			if (logCommands) Broodwar->sendText("Unit:%d cancelTrain()", unitID);
			unit->cancelTrain();
			break;
		// virtual bool cancelTrain(int slot) = 0;
		case 34:
			if (logCommands) Broodwar->sendText("Unit:%d cancelTrain(%d)", unitID, arg0);
			unit->cancelTrain(arg0);
			break;
		// virtual bool cancelAddon() = 0;
		case 35:
			if (logCommands) Broodwar->sendText("Unit:%d cancelAddon()", unitID);
			unit->cancelAddon();
			break;
		// virtual bool cancelResearch() = 0;
		case 36:
			if (logCommands) Broodwar->sendText("Unit:%d cancelResearch()", unitID);
			unit->cancelResearch();
			break;
		// virtual bool cancelUpgrade() = 0;
		case 37:
			if (logCommands) Broodwar->sendText("Unit:%d cancelUpgrade()", unitID);
			unit->cancelUpgrade();
			break;
		// virtual bool useTech(TechType tech) = 0;
		case 38:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d)", unitID, arg0);
				unit->useTech(getTechType(arg0));
			}
			break;
		// virtual bool useTech(TechType tech, Position position) = 0;
		case 39:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d, %d, %d)", unitID, arg0, arg1, arg2);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d, %d, %d)", unitID, arg0, arg1, arg2);
				unit->useTech(getTechType(arg0), getPosition(arg1, arg2));
			}
			break;
		// virtual bool useTech(TechType tech, Unit* target) = 0;
		case 40:
			if (getTechType(arg0) == NULL || getUnit(arg1) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d, %d)", unitID, arg0, arg1);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d, %d)", unitID, arg0, arg1);
				unit->useTech(getTechType(arg0), getUnit(arg1));
			}
			break;
		default:
			break;
	}
}

/**
 * Called at the end of a game. This is where we shut down sockets and clean
 * up any data structures we have created.
 */
void ExampleAIModule::onEnd() 
{
	if (proxyBotSocket == -1) {
		return;
	}

	closesocket(proxyBotSocket);
}

void ExampleAIModule::onAddUnit(Unit* unit)
{
	
}

/**
 * Removes the unit from the ID->unit mapping
 */
void ExampleAIModule::onRemove(BWAPI::Unit* unit)
{
	int key = unitMap.erase(unit);
	unitIDMap.erase(key);
}

bool ExampleAIModule::onSendText(std::string text)
{
	return true;
}

/**
 * Utility function for constructing a Position.
 *
 * Note: positions are in pixel coordinates, while the inputs are given in tile coordinates
 */
Position getPosition(int x, int y)
{
	return BWAPI::Position(pixelsPerTile*x, pixelsPerTile*y);
}

/**
 * Utility function for constructing a TilePosition.
 *
 * Note: not sure if this is correct, is there a way to get a tile position
 *       object from the api rather than create a new one?
 */
TilePosition getTilePosition(int x, int y)
{
	return BWAPI::TilePosition(x, y);
}

/**
 * Utiliity function for int to string conversion.
 */
std::string toString(int value) 
{
	std::stringstream ss;
	ss << value;
	return ss.str();
}

/**
 * Utiliity function for bool to string conversion.
 */
std::string toString(bool value) 
{
	if (value) return std::string("1");
	else return std::string("0");
}

/**
 * Returns the unit based on the unit ID
 */
Unit* getUnit(int unitID)
{
	return unitIDMap[unitID];
}

/** 
 * Returns the unit type from its identifier
 */
UnitType getUnitType(int type) 
{
	return unitTypeMap[type];
}

/** 
 * Returns the tech type from its identifier
 */
TechType getTechType(int type) 
{
	return techTypeMap[type];
}

/** 
 * Returns the upgrade type from its identifier
 */
UpgradeType getUpgradeType(int type)
{
	return upgradeTypeMap[type];
}

/**
 * Utility function for appending data to a file.
 */
void append(FILE *log, std::string data) {
	data += "\n";
	fprintf(log, (char*)data.c_str());
	fflush(log);
}

/**
 * Builds the mapping of Indices to actual BW objects.
 */
void loadTypeMaps() 
{
  std::set<UnitType> types = UnitTypes::allUnitTypes();
  for(std::set<UnitType>::iterator i=types.begin();i!=types.end();i++)
  {
	  unitTypeMap[i->getID()] = (*i);
  }

  std::set<TechType> tektypes = TechTypes::allTechTypes();
  for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++)
  {
	  techTypeMap[i->getID()] = (*i);
  }

  std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
  for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++)
  {
	  upgradeTypeMap[i->getID()] = (*i);
  }
}

/**
 * Exports static data about UnitTypes, TypeTypes, and UpgradeTypes to a text 
 * file name "TypeData.txt"
 */
void exportStaticData() {

  FILE *typeData = 0;
  typeData = fopen("TypeData.txt", "w");

  // Export unit type data
  append(typeData, "UnitTypes");
  append(typeData, "-id,race,name,mins,gas,hitPoints,shields,energy,buildTime,canAttack,canMove,width,height,supply,supplyProvided,sight,groundMaxRange,groundMinRange,groundDamage,airRange,airDamage,isBuilding,isFlyer,isSpellCaster,isWorker,whatBuilds");

  std::set<UnitType> types = UnitTypes::allUnitTypes();
  for(std::set<UnitType>::iterator i=types.begin();i!=types.end();i++)
  {
	  int id = i->getID();
	  std::string race = i->getRace().getName();
	  std::string name = i->getName();
	  int minerals = i->mineralPrice();
	  int gas = i->gasPrice();
	  int hitPoints = i->maxHitPoints()/256;
	  int shields = i->maxShields();
	  int energy = i->maxEnergy();
	  int buildTime = i->buildTime();
	  bool canAttack = i->canAttack();
	  bool canMove = i->canMove();
	  int width = i->tileWidth();
	  int height = i->tileHeight();
	  int supplyRequired = i->supplyRequired();
	  int supplyProvided = i->supplyProvided();
	  int sightRange = i->sightRange();
	  int groundMaxRange = i->groundWeapon()->maxRange();
	  int groundMinRange = i->groundWeapon()->minRange();
	  int groundDamage = i->groundWeapon()->damageAmount();
	  int airRange = i->airWeapon()->maxRange();
	  int airDamage = i->airWeapon()->damageAmount();
	  bool isBuilding = i->isBuilding();
	  bool isFlyer = i->isFlyer();
	  bool isSpellCaster = i->isSpellcaster();
	  bool isWorker = i->isWorker();
	  int whatBuilds = i->whatBuilds().first->getID();

	  std::string unitType(" UnitType");
	  unitType += ":" + toString(id)
			  + ":" + race
			  + ":" + name
			  + ":" + toString(minerals)
			  + ":" + toString(gas)
			  + ":" + toString(hitPoints)
			  + ":" + toString(shields)
			  + ":" + toString(energy)
			  + ":" + toString(buildTime)
			  + ":" + toString(canAttack)
			  + ":" + toString(canMove)
			  + ":" + toString(width)
			  + ":" + toString(height)
			  + ":" + toString(supplyRequired)
			  + ":" + toString(supplyProvided)
			  + ":" + toString(sightRange)
			  + ":" + toString(groundMaxRange)
			  + ":" + toString(groundMinRange)
			  + ":" + toString(groundDamage)
			  + ":" + toString(airRange)
			  + ":" + toString(airDamage)
			  + ":" + toString(isBuilding)
			  + ":" + toString(isFlyer)
			  + ":" + toString(isSpellCaster)
			  + ":" + toString(isWorker)
			  + ":" + toString(whatBuilds);

	  append(typeData, unitType);
  }

  // Export tech types
  append(typeData, "TechTypes");
  append(typeData, "-id,name,whatResearches,minerals,gas");

  std::set<TechType> tektypes = TechTypes::allTechTypes();
  for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++)
  {
	  int id = i->getID();
	  std::string name = i->getName();
	  int whatResearchesID = i->whatResearches()->getID(); 
	  int mins = i->mineralPrice();
	  int gas = i->gasPrice();

	  std::string techType(" TechType"); 
	  techType += ":" + toString(id)
				+ ":" + name
				+ ":" + toString(whatResearchesID)
				+ ":" + toString(mins)
				+ ":" + toString(gas);

	  append(typeData, techType);
  }

  // Export upgrade types
  append(typeData, "UpgradeTypes");
  append(typeData, "-id,name,whatUpgrades,repeats,minBase,minFactor,gasBase,gasFactor");

  std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
  for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++)
  {
	  int id = i->getID();
	  std::string name = i->getName();
	  int whatUpgradesID = i->whatUpgrades()->getID(); // unit type id of what researches it
	  int repeats = i->maxRepeats();
	  int minBase = i->mineralPriceBase();
	  int minFactor = i->mineralPriceFactor();
	  int gasBase = i->gasPriceBase();
	  int gasFactor = i->gasPriceFactor();
	  
	  std::string upgradeType(" UpgradeType"); 
	  upgradeType += ":" + toString(id)
				  + ":" + name
				  + ":" + toString(whatUpgradesID)
				  + ":" + toString(repeats)
				  + ":" + toString(minBase)
				  + ":" + toString(minFactor)
				  + ":" + toString(gasBase)
				  + ":" + toString(gasFactor);

	  append(typeData, upgradeType);
  }
}

/**
 * Establishes a connection with the ProxyBot.
 *
 * Returns -1 if the connection fails
 */
int initSocket() 
{
      int sockfd;
      int size;
      struct hostent *h;
      struct sockaddr_in client_addr;
      char myname[256];
      WORD wVersionRequested;
      WSADATA wsaData;

      wVersionRequested = MAKEWORD( 1, 1 );
      WSAStartup( wVersionRequested, &wsaData );
      gethostname(myname, 256);      
      h=gethostbyname(myname);

      size = sizeof(client_addr);
      memset(&client_addr , 0 , sizeof(struct sockaddr_in));
      memcpy((char *)&client_addr.sin_addr , h -> h_addr ,h -> h_length);
     
	  client_addr.sin_family = AF_INET;
      client_addr.sin_port = htons(PORTNUM);
      client_addr.sin_addr =  *((struct in_addr*) h->h_addr) ;
      if ((sockfd = socket(AF_INET , SOCK_STREAM , 0)) == -1){
		  return -1;
      }

      if ((connect(sockfd , (struct sockaddr *)&client_addr , sizeof(client_addr))) == -1){
		  return -1;
	  }

	  return sockfd;
}