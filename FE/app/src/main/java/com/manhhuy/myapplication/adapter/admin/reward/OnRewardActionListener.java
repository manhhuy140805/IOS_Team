package com.manhhuy.myapplication.adapter.admin.reward;

import com.manhhuy.myapplication.model.RewardItem;

public interface OnRewardActionListener {
    void onEditClick(RewardItem reward, int position);

    void onPauseClick(RewardItem reward, int position);

    void onDeleteClick(RewardItem reward, int position);
}
 