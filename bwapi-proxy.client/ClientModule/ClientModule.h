#pragma once
#include <BWAPI.h>
#include <BWTA.h>
class ClientModule : public BWAPI::AIModule
{
public:
  virtual void onStart();
  virtual void onFrame();
  virtual void onEnd();
  virtual void onUnitCreate(BWAPI::Unit* unit);
  virtual void onUnitDestroy(BWAPI::Unit* unit);
  virtual bool onSendText(std::string text);
};