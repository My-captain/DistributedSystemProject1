# 厦门大学《网络编程与分布式计算》作业一
- ServerManager同时启动UDP、TCP监听线程
    - UDP负责接受注册请求
    - TCP负责接受登录请求
    - 同时后台启动SerializeThread,负责定时将注册的用户序列化到本地(没有考虑容错, 只是简单地全量序列化)
- UDPClient负责发送注册请求
- TCPClient负责发送登录请求