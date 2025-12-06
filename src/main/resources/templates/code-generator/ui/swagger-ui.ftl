@ApiOperation(value = "${classInfo.classComment}", notes = "${classInfo.classComment}")
    @ApiImplicitParams({
            <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#list classInfo.fieldList as fieldItem >
                @ApiImplicitParam(name = "${fieldItem.fieldName}", value = "${fieldItem.fieldComment}", required = false, dataType = "${fieldItem.fieldClass}")<#if fieldItem_has_next>,</#if>
                </#list>
            </#if>
    }
    )
