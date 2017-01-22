package ru.merkulyevsasha.github.ui.repodetails;

import java.util.List;

import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.ui.MvpView;


interface MvpDetailsListView extends MvpView{

    void showList(List<CommitInfo> commits);

}
