### 开发规范 
详细参考《阿里巴巴Java开发规范》
> #### 命名
* 常量命名全部大写，单词间用下划线隔开，力求语义表达完整清楚，不要嫌名字长。
* 杜绝完全不规范的缩写，避免望文不知义
* Service/DAO 层方法命名规约
  * 获取单个对象的方法用 `get` 做前缀。
  * 获取多个对象的方法用 `list` 做前缀。
  * 获取统计值的方法用 `count` 做前缀。
  * 插入的方法用 `save/insert` 做前缀。
  * 删除的方法用 `remove/delete` 做前缀。
  * 修改的方法用 `update` 做前缀。

> #### 接口
* 接口定义需遵循REST风格
  * GET方法用来获取资源
  * POST方法可用来创建一个资源
  * PUT方法可用来更新资源
  * DELETE方法用于删除资源
  
> #### 鉴权
* 访问/user/login 接口传递username、password获取token
   ```
   POST http://localhost:8881/user/login
   
   参数：
   {
   	"username":"admin",
   	"password":"admin"
   }
   ```
* 其他接口访问需在http请求header中携带token
    ```
    GET http://localhost:8881/test/sayHello
    
    header:
    
    Authorization: 登录接口返回的token值
    ``` 
    
> #### Swagger配置
* 若服务依赖了sofn-common公用库，只需在配置文件application-*.yml中加入
    如下配置即可
    ```
    # swagger配置
    swagger:
      # 扫描包
      base-package: com.sofn.sys.web
      # 名称
      name: 系统管理
      # 接口前缀
      prefix: /sys
    ```
> #### Excel导入导出工具
* 如果服务依赖了sofn-common公用库，只需要在类上添加相关注解就可以使用Excel工具了
* Ecxcel测试方法请看com.sofn.common.excel.test.ExcelUtilTest 类
* Excel实体类请参考com.sofn.common.excel.test.testimport.ImportUserBean

