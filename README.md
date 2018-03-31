[![License](https://img.shields.io/badge/license-APACHE2.0-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/sutine/webant/pulls)
[![GitHub stars](https://img.shields.io/github/stars/sutine/webant.svg?style=social&label=Stars)](https://github.com/sutine/webant)
[![GitHub forks](https://img.shields.io/github/forks/sutine/webant.svg?style=social&label=Fork)](https://github.com/sutine/webant)

## webant简介
webant是一套易扩展，易部署，易管理的网络爬虫系统。webant意指“web ant”，即“网络蚂蚁”，寓意有很多蚂蚁在网络上爬走，采集有用的信息。

webant具备如下主要特性：

1. 支持多种部署模式。运行简单任务或调试时可以单进程部署，也可以嵌入到别的应用程序中通过API进行管理，也可以部署为服务器并由提供的客户端进行管理，也可以支持分布式部署并通过蚁后节点管理整个集群。
2. 支持多任务，多站点管理。可以同时运行多个任务，每个任务可以同时爬取一个或多个站点，任务和站点由配置文件进行描述，大大减少开发工作量。支持对任务和站点进行启动、暂停、停止等实时进度管理。
3. 支持插件式扩展爬虫逻辑。自定义数据采集的内容和逻辑，以极少的工作量完成一个大规模爬虫的开发和部署工作。
4. 可以作为一个数据同步系统，支持多种格式的数据源。包括web网络上的网站内容、数据库、邮箱、各种格式的文档、ftp等等，并且支持扩展以解析新的格式。
5. 支持多种数据持久化形式。内置支持嵌入式数据库H2、Hsqldb、Berkeleydb，json文件，mysql，elasticsearch等不同的持久化形式。
