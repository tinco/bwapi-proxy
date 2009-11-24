package bwapiproxy.core;
/**
 * Class for sotring misc. constants.
 * 
 * To get the constant value of an enum, use ordinal. 
 *  - e.g. int raceID = Race.Zerg.ordinal()
 */
public class Constants {

	/** 
	 * Races 
	 */
	public enum Race {
		Zerg,
		Terran,
		Protoss,
		Random,
		Other,
		None,
		Unknown
	}

	/** 
	 * Orders (commands)
	 */
	public enum Order {	
		Die,
	    Stop,
	    Guard,
	    PlayerGuard,
	    TurretGuard,
	    BunkerGuard,
	    Move,
	    ReaverStop,
	    Attack1,
	    Attack2,
	    AttackUnit,
	    AttackFixedRange,
	    AttackTile,
	    Hover,
	    AttackMove,
	    InfestMine1,
	    Nothing1,
	    Powerup1,
	    TowerGuard,
	    TowerAttack,
	    VultureMine,
	    StayinRange,
	    TurretAttack,
	    Nothing2,
	    Nothing3,
	    DroneStartBuild,
	    DroneBuild,
	    InfestMine2,
	    InfestMine3,
	    InfestMine4,
	    BuildTerran,
	    BuildProtoss1,
	    BuildProtoss2,
	    ConstructingBuilding,
	    Repair1,
	    Repair2,
	    PlaceAddon,
	    BuildAddon,
	    Train,
	    RallyPoint1,
	    RallyPoint2,
	    ZergBirth,
	    Morph1,
	    Morph2,
	    BuildSelf1,
	    ZergBuildSelf,
	    Build5,
	    Enternyduscanal,
	    BuildSelf2,
	    Follow,
	    Carrier,
	    CarrierIgnore1,
	    CarrierStop,
	    CarrierAttack1,
	    CarrierAttack2,
	    CarrierIgnore2,
	    CarrierFight,
	    HoldPosition1,
	    Reaver,
	    ReaverAttack1,
	    ReaverAttack2,
	    ReaverFight,
	    ReaverHold,
	    TrainFighter,
	    StrafeUnit1,
	    StrafeUnit2,
	    RechargeShields1,
	    Rechargeshields2,
	    ShieldBattery,
	    Return,
	    DroneLand,
	    BuildingLand,
	    BuildingLiftoff,
	    DroneLiftoff,
	    Liftoff,
	    ResearchTech,
	    Upgrade,
	    Larva,
	    SpawningLarva,
	    Harvest1,
	    Harvest2,
	    MoveToGas, // Unit is moving to refinery
	    WaitForGas, // Unit is waiting to enter the refinery (another unit is currently in it)
	    HarvestGas, // Unit is in refinery
	    ReturnGas, // Unit is returning gas to center
	    MoveToMinerals, // Unit is moving to mineral patch
	    WaitForMinerals, // Unit is waiting to use the mineral patch (another unit is currently mining from it)
	    MiningMinerals, // Unit is mining minerals from mineral patch
	    Harvest3,
	    Harvest4,
	    ReturnMinerals, // Unit is returning minerals to center
	    Harvest5,
	    EnterTransport,
	    Pickup1,
	    Pickup2,
	    Pickup3,
	    Pickup4,
	    Powerup2,
	    SiegeMode,
	    TankMode,
	    WatchTarget,
	    InitCreepGrowth,
	    SpreadCreep,
	    StoppingCreepGrowth,
	    GuardianAspect,
	    WarpingArchon,
	    CompletingArchonsummon,
	    HoldPosition2,
	    HoldPosition3,
	    Cloak,
	    Decloak,
	    Unload,
	    MoveUnload,
	    FireYamatoGun1,
	    FireYamatoGun2,
	    MagnaPulse,
	    Burrow,
	    Burrowed,
	    Unburrow,
	    DarkSwarm,
	    CastParasite,
	    SummonBroodlings,
	    EmpShockwave,
	    NukeWait,
	    NukeTrain,
	    NukeLaunch,
	    NukePaint,
	    NukeUnit,
	    NukeGround,
	    NukeTrack,
	    InitArbiter,
	    CloakNearbyUnits,
	    PlaceMine,
	    Rightclickaction,
	    SapUnit,
	    SapLocation,
	    HoldPosition4,
	    Teleport,
	    TeleporttoLocation,
	    PlaceScanner,
	    Scanner,
	    DefensiveMatrix,
	    PsiStorm,
	    Irradiate,
	    Plague,
	    Consume,
	    Ensnare,
	    StasisField,
	    Hallucianation1,
	    Hallucination2,
	    ResetCollision1,
	    ResetCollision2,
	    Patrol,
	    CTFCOPInit,
	    CTFCOP1,
	    CTFCOP2,
	    ComputerAI,
	    AtkMoveEP,
	    HarassMove,
	    AIPatrol,
	    GuardPost,
	    RescuePassive,
	    Neutral,
	    ComputerReturn,
	    InitPsiProvider,
	    SelfDestrucing,
	    Critter,
	    HiddenGun,
	    OpenDoor,
	    CloseDoor,
	    HideTrap,
	    RevealTrap,
	    Enabledoodad,
	    Disabledoodad,
	    Warpin,
	    Medic,
	    MedicHeal1,
	    HealMove,
	    MedicHoldPosition,
	    MedicHeal2,
	    Restoration,
	    CastDisruptionWeb,
	    CastMindControl,
	    WarpingDarkArchon,
	    CastFeedback,
	    CastOpticalFlare,
	    CastMaelstrom,
	    JunkYardDog,
	    Fatal,
	    None,
	    Unknown 
	}
}
