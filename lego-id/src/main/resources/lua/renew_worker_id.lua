local prefix = ARGV[1]
local clientId = ARGV[2]
local ttl = tonumber(ARGV[3])
local workerId = tonumber(ARGV[4])

local key = prefix .. ":" .. workerId

if redis.call('GET', key) == clientId then
    if redis.call('EXPIRE', key, ttl) == 1 then
        return workerId
    end
end

return -1
