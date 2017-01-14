package ru.merkulyevsasha.github.mvp.repodetails;

import java.util.List;

import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.mvp.MvpView;


interface MvpDetailsListView extends MvpView{

    void showList(List<CommitInfo> commits);

}
