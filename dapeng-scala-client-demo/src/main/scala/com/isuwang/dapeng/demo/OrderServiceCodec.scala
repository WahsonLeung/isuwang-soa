package com.isuwang.dapeng.demo

import com.isuwang.dapeng.core._
import com.isuwang.org.apache.thrift._
import com.isuwang.org.apache.thrift.protocol._
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Optional

/**
  * Autogenerated by Dapeng-Code-Generator (1.1.0)
  * <p>
  * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
  *
  * @generated
  **/
class OrderServiceCodec {

  class OrderSerializer extends TScalaBeanSerializer[com.isuwang.dapeng.demo.domain.Order] {
    override def read(iproto: TProtocol): com.isuwang.dapeng.demo.domain.Order = {

      iproto.readStructBegin

      var schemeField: com.isuwang.org.apache.thrift.protocol.TField = null

      var id: Int = 0
      var orderNo: String = null
      var payType: com.isuwang.dapeng.demo.enums.PayTypeEnum.PayTypeEnum = null
      var buyerId: Int = 0
      var sellerId: Int = 0
      var remark: Option[String] = null

      while (schemeField == null || schemeField.`type` != com.isuwang.org.apache.thrift.protocol.TType.STOP) {
        schemeField = iproto.readFieldBegin

        schemeField.id match {
          case 1 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.I32 => id = iproto.readI32
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case 2 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.STRING => orderNo = iproto.readString
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case 3 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.I32 =>
                payType = com.isuwang.dapeng.demo.enums.PayTypeEnum.findByValue(iproto.readI32)
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case 4 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.I32 => buyerId = iproto.readI32
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case 5 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.I32 => sellerId = iproto.readI32
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case 6 =>
            schemeField.`type` match {
              case com.isuwang.org.apache.thrift.protocol.TType.STRING =>
                val elem0 = iproto.readString
                remark = Some(elem0)
              case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
            }
          case _ => com.isuwang.org.apache.thrift.protocol.TProtocolUtil.skip(iproto, schemeField.`type`)
        }

        iproto.readFieldEnd
      }

      iproto.readStructEnd

      val bean = com.isuwang.dapeng.demo.domain.Order(id, orderNo, payType, buyerId, sellerId, remark)
      validate(bean)

      bean
    }

    override def write(bean: com.isuwang.dapeng.demo.domain.Order, oproto: TProtocol): Unit = {
      validate(bean)
      oproto.writeStructBegin(new com.isuwang.org.apache.thrift.protocol.TStruct("Order"))

      oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("id",com.isuwang.org.apache.thrift.protocol.TType.I32, 1.asInstanceOf[Short]))
      oproto.writeI32(bean.id)
      oproto.writeFieldEnd

      oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("orderNo", com.isuwang.org.apache.thrift.protocol.TType.STRING, 2.asInstanceOf[Short]))
      oproto.writeString(bean.orderNo)
      oproto.writeFieldEnd

      oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("payType", com.isuwang.org.apache.thrift.protocol.TType.I32, 3.asInstanceOf[Short]))
      oproto.writeI32(bean.payType.id)
      oproto.writeFieldEnd

      oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("buyerId", com.isuwang.org.apache.thrift.protocol.TType.I32, 4.asInstanceOf[Short]))
      oproto.writeI32(bean.buyerId)
      oproto.writeFieldEnd

      oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("sellerId", com.isuwang.org.apache.thrift.protocol.TType.I32, 5.asInstanceOf[Short]))
      oproto.writeI32(bean.sellerId)
      oproto.writeFieldEnd

      bean.remark.map {elem0 =>
        oproto.writeFieldBegin(new com.isuwang.org.apache.thrift.protocol.TField("remark", com.isuwang.org.apache.thrift.protocol.TType.STRING, 6.asInstanceOf[Short]))
        oproto.writeString(elem0)
        oproto.writeFieldEnd
      }

      oproto.writeFieldStop
      oproto.writeStructEnd
    }

    override def validate(bean: com.isuwang.dapeng.demo.domain.Order): Unit = {
      if (bean.orderNo == null)
        throw new SoaException(SoaBaseCode.NotNull, "orderNo字段不允许为空")
      if (bean.payType == null)
        throw new SoaException(SoaBaseCode.NotNull, "payType字段不允许为空")
    }

    override def toString(bean: com.isuwang.dapeng.demo.domain.Order): String = {
      if (bean == null) "null" else bean.toString
    }
  }

}

