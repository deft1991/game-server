# Basic game server. 

Contains multimodule structure. You can use each module as independent service. 
The main idea is to prepare generic game-server and adopt it to different games.

### TODOs

I will try to add all these modules. For test purposes I will use H2 in-memory DataBase.
Also I'm going to add Dev profile with local PostgresDB. You can configure your DB settings.

### Auth service

Main idea is to create auth service to sign-up/sign-in/sign-out.
Then we can add this service as a lib to any other services. 
I'm going to use Session Tokens. I will store tokens in PostgresDB and also I will use Reddis
as a cache layer for tokens. 

Here is a userRedis flow:

