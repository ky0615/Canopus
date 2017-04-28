package moe.linux.boilerplate.di

import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import moe.linux.boilerplate.di.scope.Account
import moe.linux.boilerplate.di.scope.Toot


@Module
class RealmModule {

    @Provides @Account
    fun provideAccountRealm(): Realm = Realm.getInstance(RealmConfiguration.Builder().name("account").build())

    @Provides @Toot
    fun provideTootRealm(): Realm = Realm.getInstance(RealmConfiguration.Builder().name("toot").build())
}