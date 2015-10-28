function updateWorld(world)
    if math.random(1, 400) == 1 then
        world:bonus(math.random(1, 9), math.random(1, 9))
    end
end