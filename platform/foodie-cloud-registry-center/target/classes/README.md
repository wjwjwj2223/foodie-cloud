## 本地测试启动顺序
先把redis 和 mysql 以及 rabbitMQ 开启
1. foodie-cloud-register
2. config-server
3. auth-service
4. 应用服务(foodie-user-service等)
5. 网关服务(gateway)