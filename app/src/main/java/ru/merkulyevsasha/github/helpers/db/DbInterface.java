package ru.merkulyevsasha.github.helpers.db;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;

public interface DbInterface {

    ArrayList<Repo> getRepos(String login);
    ArrayList<Repo> searchRepos(String login, String searchText);
    void saveRepos(String login, ArrayList<Repo> repos);
    void cleanRepos(String login);
}
