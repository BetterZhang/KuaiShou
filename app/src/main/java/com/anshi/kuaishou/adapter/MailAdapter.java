package com.anshi.kuaishou.adapter;

import android.support.annotation.Nullable;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.domain.MailInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/16 下午 3:13
 * Desc   : description
 */
public class MailAdapter extends BaseQuickAdapter<MailInfo, BaseViewHolder> {

    public MailAdapter(@Nullable List<MailInfo> data) {
        super(R.layout.item_mail, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MailInfo item) {
        if (item == null)
            return;
        helper.setText(R.id.tv_take_code, "" + item.getTakeCode())
                .setText(R.id.tv_phone, item.getRecipientMobile())
                .setText(R.id.tv_mailno, item.getMailNo())
                .setText(R.id.tv_name, item.getRecipientName())
                .setText(R.id.tv_parcelId, "" + item.getParcelId())
                .addOnClickListener(R.id.btn_out);

    }
}
