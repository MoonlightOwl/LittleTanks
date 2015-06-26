local gameWorld

function init(world)
    world:message("Defend your base!")
    gameWorld = world
end

function updateTank(tank)
    if tank:getY() > 6 then
        gameWorld:defeat()
    else
        local action = math.random(1, 20)
        if action == 1 then
            tank:move(0, 1)
        elseif action == 2 then
            tank:fire()
        end
    end
end

function updateWorld(world)
    if math.random(1,200) == 1 then
        world:spawn(math.random(1,9), math.random(1, 3), math.random(1,2))
    end
end