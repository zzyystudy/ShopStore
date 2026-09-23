<#ftl output_format="HTML">
<div style="font-family: Arial, 'Microsoft YaHei', sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">

    <!-- 标题栏 -->
    <div style="background-color: #4CAF50; color: #ffffff; padding: 20px; text-align: center;">
        <h2 style="margin: 0; font-size: 20px;">订单支付成功</h2>
    </div>

    <!-- 内容区 -->
    <div style="padding: 24px; color: #333333; line-height: 1.6;">
        <p style="margin: 0 0 16px;">您好，您的订单已支付成功。</p>
        <p style="margin: 0 0 16px;">订单号：<span style="font-weight: bold; color: #4CAF50;">${shopOrderNo!''}</span></p>

        <table style="width: 100%; border-collapse: collapse; margin-top: 16px; font-size: 14px;">
            <thead>
            <tr style="background-color: #f5f5f5;">
                <th style="padding: 10px; border: 1px solid #e0e0e0; text-align: left;">商品名称</th>
                <th style="padding: 10px; border: 1px solid #e0e0e0; text-align: left;">商品信息</th>
            </tr>
            </thead>
            <tbody>
            <#list virtualGoodsVOList![] as goods>
                <tr>
                    <td style="padding: 10px; border: 1px solid #e0e0e0;">${goods.productName!''}</td>
                    <td style="padding: 10px; border: 1px solid #e0e0e0;">${goods.context!''}</td>
                </tr>
            </#list>
            </tbody>
        </table>

        <p style="margin: 24px 0 0; font-size: 12px; color: #999999;">此为系统自动发送邮件，请勿回复。</p>
    </div>
</div>