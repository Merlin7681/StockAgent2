## 运行前端工程

### 1、安装Node.js

Node.js是一个基于JavaScript引擎的服务器端环境，前端项目在开发环境下要基于Node.js来运行

安装：node



### 2、配置npm镜像

打开命令行，配置依赖的下载使用阿里镜像

```bash
npm config set registry https://registry.npmmirror.com
```



### 3、运行前端项目

进入项目目录，执行下面的命令启动项目：

```powershell
cd xiaozhi-ui
npm i
npm run dev
```

### 4. 对接的后端地址
前端对后端地址的配置看两个地方：
- vite.config.js（proxy），Vue的路由配置
- ChatWindow.vue（axios.post）
~~~
http://localhost:80
# 聊天接口：Vue路由（vite.config.js）时会取去掉api，接口地址就成如下：
http://localhost:80/chat
~~~

