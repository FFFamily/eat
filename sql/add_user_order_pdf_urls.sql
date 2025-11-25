ALTER TABLE `user_order`
    ADD COLUMN `delivery_note_pdf_url` VARCHAR(500) NULL COMMENT '交付单PDF URL' AFTER `delivery_photo`,
    ADD COLUMN `settlement_pdf_url` VARCHAR(500) NULL COMMENT '结算单PDF URL' AFTER `delivery_note_pdf_url`,
    ADD COLUMN `application_pdf_url` VARCHAR(500) NULL COMMENT '申请单PDF URL' AFTER `settlement_pdf_url`;


