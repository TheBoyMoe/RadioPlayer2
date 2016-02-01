package com.example.radioplayer.event;

import com.example.radioplayer.model.Category;

import java.util.List;

public class CategoryThreadCompletionEvent extends BaseEvent{

    private List<Category> mCategoryList;

    public CategoryThreadCompletionEvent(List<Category> categoryList) {
        mCategoryList = categoryList;
    }

    public List<Category> getCategoryList() {
        return mCategoryList;
    }
}
