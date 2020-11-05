1.环境要求:
jdk1.8，和离线图片放置于同一服务器，并预先通过nginx发布图片

2.配置文件application.properties说明
server.port: 程序端口
viewlib.addr: 现场视图库地址
viewlib.batchNum: 每次给视图库录入多少条数据（根据现场视图库部署情况配置，以防入库提取特征值失败）
flag.uploadPicture: 是否需要上传图片到haioumate
flag.uploadMsg: 是否将人员信息录入视图库，推荐先填写false，空跑一遍查阅日志，确认身份证和姓名解析正确，图片地址能访问到图片内容，之后再改为true录入
prefix.pictureUrl: 图片访问url前缀
location.picturePath: nginx配置的代理路径
needCheckIDNumber: 是否需要校验身份证，如需要校验会过滤身份证不合法的数据
pictureName.format: 离线图片命名格式，NAME和ID需大写
pictureName.split: 离线数据的分隔符

3.启动命令
nohup java -jar kmtool-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &

4.接口掉用
POST请求;http://ip:port/uploadPic/uploadLocalPic?tabID=XXX&location=XXX
tabID为需要录入的目标人员库ID，可查询mysql数据库haiou的db中的a_haiou_repository表或者cdb中的dataclasstab表
location为离线图片路径

5.日志说明
可根据现场情况修改logback.xml中的<property name="LOG_HOME" value="D:/opt/haiou/logs" />配置来自定义日志输出路径

