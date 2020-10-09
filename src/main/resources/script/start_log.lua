local key = KEYS[1];
local map = ARGV[1];
for i, j in ipairs(map) do
    if i % 2 == 0 then
        redis.call('hsetnx', key, i,j)
    else
        key = j
    end
end
return 1
