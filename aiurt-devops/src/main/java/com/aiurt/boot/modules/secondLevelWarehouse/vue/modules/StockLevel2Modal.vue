<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物资编号">
          <a-input placeholder="请输入物资编号" v-decorator="['materialCode', validatorRules.materialCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物资名称">
          <a-input placeholder="请输入物资名称" v-decorator="['materialName', validatorRules.materialName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物资类型（1：非生产类型 2：生产类型）">
          <a-input-number v-decorator="[ 'type', validatorRules.type ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="规格&型号">
          <a-input placeholder="请输入规格&型号" v-decorator="['specifications', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="原产地">
          <a-input placeholder="请输入原产地" v-decorator="['countryOrigin', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="生产商">
          <a-input placeholder="请输入生产商" v-decorator="['manufacturer', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="品牌">
          <a-input placeholder="请输入品牌" v-decorator="['brand', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="单位">
          <a-input placeholder="请输入单位" v-decorator="['unit', validatorRules.unit ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="数量">
          <a-input-number v-decorator="[ 'num', validatorRules.num ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="仓库编号">
          <a-input placeholder="请输入仓库编号" v-decorator="['warehouseCode', validatorRules.warehouseCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="仓库名称">
          <a-input placeholder="请输入仓库名称" v-decorator="['warehouseName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态(0.未删除 1.已删除)">
          <a-input-number v-decorator="[ 'delFlag', validatorRules.delFlag ]" />
        </a-form-item>
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "StockLevel2Modal",
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        materialCode:{rules: [{ required: true, message: '请输入物资编号!' }]},
        materialName:{rules: [{ required: true, message: '请输入物资名称!' }]},
        type:{rules: [{ required: true, message: '请输入物资类型（1：非生产类型 2：生产类型）!' }]},
        unit:{rules: [{ required: true, message: '请输入单位!' }]},
        num:{rules: [{ required: true, message: '请输入数量!' }]},
        warehouseCode:{rules: [{ required: true, message: '请输入仓库编号!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态(0.未删除 1.已删除)!' }]},
        },
        url: {
          add: "/secondLevelWarehouse/stockLevel2/add",
          edit: "/secondLevelWarehouse/stockLevel2/edit",
        },
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'materialCode','materialName','type','specifications','countryOrigin','manufacturer','brand','unit','num','warehouseCode','warehouseName','delFlag'))
		  //时间格式化
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>