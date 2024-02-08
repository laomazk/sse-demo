## SSE 服务发送事件

传统的网页都是浏览器向服务器“查询”数据，但是很多场合，最有效的方式时服务器向浏览器“发送”数据。比如，每当收到新的电子邮件，服务器就向浏览器发送一个“通知”，这要比浏览器按时向服务器查询（polling）更有效率。

服务器发送事件（Server-Sent Event，简称SSE）就是为了解决这个问题，而提出的一种新 API，部署在EventSource对象上。目前，除了 IE，其他主流浏览器都支持。

简单说，所谓SSE，就是浏览器向服务器发送一个HTTP请求，然后服务器不断单向地向浏览器推送“信息”（message）。这种信息在格式上很简单，就是“信息”加上前缀“data:”，然后以“\n\n”结尾。

```bash
curl http://example.com/dates
data: xxxxxxx

data: zzzzzzz

data: ccccccc
```

SSE与WebSocket有相似功能，都是用来建立浏览器与服务器之间地通信渠道。两者地区别在于：

- WebSocket 是全双工通道，可以双向通信，功能更强；SSE 是单向通道，只能服务器向浏览器端发送

- WebSocket 是一种新的协议，需要服务器端支持；SSE则是部署在HTTP协议只上的，现有的服务器软件都支持。

- SSE 是一个轻量级协议，相对简单；WebSocket 是一种较重的协议，相对复杂。

- SSE 默认支持断线重连，WebSocket 则需要额外部署。

- SSE 支持自定义发送的数据类型。


SseEmitter

Spring mvc 4.2 开始提供。服务器向客户端单向发送

- 创建AtomicInteger用于记录连接数
- 创建ConcurrentHashMap用于存放连接信息
- 建立连接：创建并返回一个带有超时时间的SseEmitter给前端。超时间设为0表示永不过期
- 设置连接结束的回调方法completionCallBack
- 设置连接超时的回调方法timeoutCallBack
- 设置连接异常的回调方法errorCallBack
- 创建推送信息的方法SseEmitter.send()
- 创建移除连接的方法
