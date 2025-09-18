-- 为回收订单表添加PDF URL字段
ALTER TABLE recycle_order 
ADD COLUMN settlement_pdf_url VARCHAR(500) COMMENT '结算单PDF URL地址',
ADD COLUMN application_pdf_url VARCHAR(500) COMMENT '申请单PDF URL地址';