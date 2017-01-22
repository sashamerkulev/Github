package ru.merkulyevsasha.github.dagger2;

import javax.inject.Singleton;

import dagger.Component;
import ru.merkulyevsasha.github.ui.repodetails.DetailsActivity;
import ru.merkulyevsasha.github.ui.repolist.MainActivity;


@Singleton
@Component(modules={AppModule.class, DataModule.class})
public interface DataComponent {


    void inject(MainActivity context);
    void inject(DetailsActivity context);


}
