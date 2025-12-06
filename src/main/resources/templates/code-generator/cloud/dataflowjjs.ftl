/**
* GCP - dataflow job jjs for [${classInfo.classComment} - ${classInfo.tableName}]
* AUTHOR ${authorName}
*
* User-defined function (UDF) to transform events as part of a Dataflow template job.
* upload to GCS and create dataflow job with this js file and method as 'process'
* @param {string} inJson input Pub/Sub JSON message (stringified)
* @return {string} outJson output JSON message (stringified)
*/
function process(inJson) {
    //for local js debug
    //var obj = JSON.parse(JSON.stringify(inJson));
    //for online jjs
    var obj = JSON.parse(inJson);
    var includePubsubMessage = obj.data && obj.attributes;
    var data = includePubsubMessage ? obj.data : obj;
    //debug and show error if you need special logic
    if(data.hasOwnProperty('show_error')){
        throw new ERROR("show_error:"+JSON.stringify(data))
    }
    // INSERT CUSTOM TRANSFORMATION LOGIC HERE
    var tableObj= {};
    tableObj.insert_time=new Date().toUTCString()
<#list classInfo.fieldList as fieldItem >
    tableObj.${fieldItem.columnName}=data.${fieldItem.fieldName}
</#list>
    return JSON.stringify(tableObj);
}

//field name = field name
<#list classInfo.fieldList as fieldItem >
    tableObj.${fieldItem.fieldName}=data.${fieldItem.fieldName}
</#list>

//column name = column name
<#list classInfo.fieldList as fieldItem >
    tableObj.${fieldItem.columnName}=data.${fieldItem.columnName}
</#list>