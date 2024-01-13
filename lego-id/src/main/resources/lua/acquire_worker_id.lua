local prefix = ARGV[1]
local clientId = ARGV[2]
local ttl = tonumber(ARGV[3])
local maxWorkerId = 31
for i = 0, maxWorkerId do
    local key = prefix .. ":" .. i
    if redis.call('SETNX', key, clientId) == 1 then
        redis.call('EXPIRE', key, ttl)
        return i
    end
end

return -1
