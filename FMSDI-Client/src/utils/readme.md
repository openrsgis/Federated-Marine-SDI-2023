# 请求示例

## UrlFactory将后台请求接口进行了统一管理

```javascript{.line-numbers}
var apiUrl = this.$urlFactory.getApiURL('QUERY_DATASET')
      axios.get(apiUrl, { params }).then((response) => {
        const { data } = response
      })
```
