# Basic game server. 

Contains multimodule structure. You can use each module as independent service. 
The main idea is to prepare generic game-server and adopt it to different games.

### TODOs

I will try to add all these modules. For test purposes I will use H2 in-memory DataBase.
Also I'm going to add Dev profile with local PostgresDB. You can configure your DB settings.

### Auth service

Main idea is to create auth service to `sign-up/sign-in/sign-out`.
Then we can add this service as a lib to any other services. 
I'm going to use Session Tokens. I'm storing Users in Postgres and session tokens in Redis.

See [SessionToken.java](auth-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fdeft%2Fauthservice%2Fdata%2Fredis%2FSessionToken.java) It is session token class.

See [RedisAuthenticationFilter.java](auth-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fdeft%2Fauthservice%2Fconfiguration%2FRedisAuthenticationFilter.java) there we get token from Redis. Then we find user in PostgresDB and set `GrantedAuthority`

#### Redis as L2 Hibernate cache

Also I'm using Redis as a second cache layer in hibernate.
Configuration file for L2 cache --> [redisson.yaml](auth-service%2Fsrc%2Fmain%2Fresources%2Fredisson.yaml)

