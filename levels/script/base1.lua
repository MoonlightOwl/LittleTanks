local gameWorld

function init(world)
    world:message("Defend your base!")
    gameWorld = world
end

local function randomBool()
    return math.random(0, 1) == 1
end
local function baseContains(x, y)
    if x>3 and x<9 then
        if y>3 and y<9 then
            return true
        end
    end
    return false
end

function updateTank(tank)
    if baseContains(tank:getX(), tank:getY()) then
        gameWorld:defeat()
    else
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
end