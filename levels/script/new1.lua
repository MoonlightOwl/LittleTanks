local function generatePoint(angle)
    local x = 5.5 + math.cos(angle) * 3
    local y = 5.5 + math.sin(angle) * 3
    return x, y
end

function updateTank(tank)
    if math.random(0,1) == 1 then
        local angle = math.atan2(tank:getY() - 5.5, tank:getX() - 5.5) + math.pi/6
        tank:moveTo(generatePoint(angle))
    else
        tank:fire()
    end
end