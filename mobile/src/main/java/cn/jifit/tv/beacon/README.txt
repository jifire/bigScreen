说明：
MIBeaconServer 创建小米手环盒子本地服务端。暂时仅接收Beacon盒子上传的JSON数据。数据中仅含盒子编号和盒子周围小米手环蓝牙MAC地址。
该类仅创建SocketServer。并将Accept、Read注册到NonBlockHander的全局Selector上。

NonBlockHandler 创建一个全局Selector，用于注册非阻塞事件。简单处理事件。复杂处理均通过WorkFactory创建任务，由单线程队列去处理。

全应用由两个核心的单线程事务处理器。一个仅处理事件。保障了事件简单处理部分的线程安全。
一个仅处理异步Work，保障了数据处理的线程安全。
注意在这两个线程里，不用使用相同的对象，避免线程安全隐患，提高性能。

WorkFactory，创建Work的工厂类。注意，仅能由NonBlockHandler去调用getCallable方法，由SingleThreadQueue去消费掉任务。

TODO：对于失败的任务，需要放入队尾重新处理的，如何操作？