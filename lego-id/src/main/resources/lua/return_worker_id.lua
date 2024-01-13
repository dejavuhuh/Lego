local prefix = ARGV[1]
local clientId = ARGV[2]
local workerId = tonumber(ARGV[3])

local key = prefix .. ":" .. workerId

if redis.call('GET', key) == clientId then
    if redis.call('DEL', key) == 1 then
        return workerId
    end
end

return -1
