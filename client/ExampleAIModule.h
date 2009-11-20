#pragma once
#include <BWAPI.h>
#include <BWTA.h>
class ExampleAIModule : public BWAPI::AIModule
{
public:
  virtual void onStart();
  virtual void onFrame();
  virtual void onEnd();
  virtual void onAddUnit(BWAPI::Unit* unit);
  virtual void onRemove(BWAPI::Unit* unit);
  virtual bool onSendText(std::string text);
};