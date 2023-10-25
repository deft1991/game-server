
- [ Basic game server ](#Basic-game-server)
  - [ ToDOs ](#TODOs)
  - [ Multimodule Structure ](#Multimodule-Structure)
  - [Auth service](#Auth-service)
  - [Redis as L2 Hibernate cache](#Redis-as-L2-Hibernate-cache)
  - [Gateway](#Gateway)
- [Metrics and Monitoring](#Metrics-and-Monitoring)
  - [Metrics](#Metrics)
  - [Monitoring](#Monitoring)
- [User Flow](#User-Flow)
  - [Register](#Register)
  - [Login](#Login)

# Basic game server

Contains multimodule structure. You can use each module as independent service. 
The main idea is to prepare generic game-server and adopt it to different games.

### TODOs

I will try to add all these modules. For test purposes I will use H2 in-memory DataBase.
Also I'm going to add Dev profile with local PostgresDB. You can configure your DB settings.

### Multimodule Structure

Use it to understand how it works https://reflectoring.io/spring-boot-gradle-multi-module/

### Auth service

Main idea is to create auth service to `sign-up/sign-in/sign-out`.
Then we can add this service as a lib to any other services. 
I'm going to use Session Tokens. I'm storing Users in Postgres and session tokens in Redis.

See [SessionToken.java](auth-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fdeft%2Fauth%2Fdata%2Fredis%2FSessionToken.java) It is session token class.

See [RedisAuthenticationFilter.java](auth-service%2Fsrc%2Fmain%2Fjava%2Fcom%2Fdeft%2Fauth%2Fconfiguration%2FRedisAuthenticationFilter.java) there we get token from Redis. Then we find user in PostgresDB and set `GrantedAuthority`

#### Postgres

Use [docker-compose-postgres-and-pg-admin.yml](docker%2Fauth-service%2Fdocker-compose-postgres-and-pg-admin.yml) to start Postgres in Docker container

#### Redis as L2 Hibernate cache

Also I'm using Redis as a second cache layer in hibernate.
Configuration file for L2 cache --> [redisson.yaml](auth-service%2Fsrc%2Fmain%2Fresources%2Fredisson.yaml)
Use [docker-compose-redis-only.yml](docker%2Fauth-service%2Fdocker-compose-redis-only.yml) to start Redis in Docker

## Metrics and Monitoring

### Metrics

I'm Using Prometheus with Micrometer. 
Also I use Grafana to visualise all metrics. 

I use 'com.github.kingbbode:spring-boot-custom-yaml-importer:0.3.0' this lib to import [custom-metrics.yml](metrics-lib%2Fsrc%2Fmain%2Fresources%2Fconfig%2Fcustom-metrics.yml)
to all projects that will add metrics-lib

We cannot use micrometer annotations in filter classes. For more info see https://bwgjoseph.com/why-spring-aop-dont-work-in-filter-class .
That is why I recommend use Micrometer annotations in services and controllers.

If you are using Micrometer annotations I recommend to use pattern for naming: `name1_name2_..._nameN`. 
Later you will find your metrics in micrometer like `name1_name2_..._nameN`


### Monitoring
Add Grafana and Prometheus. I used this article https://medium.com/simform-engineering/revolutionize-monitoring-empowering-spring-boot-applications-with-prometheus-and-grafana-e99c5c7248cf

To start Grafana and Prometheus call [docker-compose-grafana-prometheus.yml](monitoring%2Fdocker-compose-grafana-prometheus.yml)
Grafana available on http://localhost:3000/ . On first start you need to login with login/password - admin/admin. And then you should change it.

Prometheus available on http://localhost:9090/

### Gateway

Check this article to understand better https://haris-zujo.medium.com/spring-cloud-gateway-request-filtering-and-redirection-9e4b6d559d1a

### User Flow

#### Register

To register a new user call POST: http://localhost:8080/auth/v1/auth/register?userName=1&userPassword=1

You need to pass userName and userPassword as path variables

As a response you'll receive SessionToken. 
With SessionToken you can call other protected resources.

#### Login

To register a new user call POST: http://localhost:8080/auth/v1/auth/login?userName=1&userPassword=1

You need to pass userName and userPassword as path variables

As a response you'll receive SessionToken. 
With SessionToken you can call other protected resources
