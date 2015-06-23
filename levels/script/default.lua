function init(world)
  math.randomseed(os.time())
end

local function randomBool()
  return math.random(0,1) == 1
end

function updateTank(tank)
  local action = math.random(1, 20)
  if action == 1 then
    local dx, dy = 0, 0
    if randomBool() then
      if randomBool() then dx = 1
      else dx = -1 end
    else
      if randomBool() then dy = 1
      else dy = -1 end
    end
    tank:move(dx, dy)
  elseif action == 2 then
    tank:fire()
  end
end
