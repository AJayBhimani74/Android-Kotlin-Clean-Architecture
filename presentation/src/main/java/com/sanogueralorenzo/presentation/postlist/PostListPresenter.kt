package com.sanogueralorenzo.presentation.postlist

import com.sanogueralorenzo.domain.usecase.UsersPostsUseCase
import com.sanogueralorenzo.presentation.IView
import com.sanogueralorenzo.presentation.Presenter
import com.sanogueralorenzo.presentation.model.PostItem
import com.sanogueralorenzo.presentation.model.PostItemMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface PostListView : IView {
    fun loading(show: Boolean)
    fun add(list: List<PostItem>)
    fun error()
}

class PostListPresenter @Inject constructor(private val useCase: UsersPostsUseCase,
                                            private val mapper: PostItemMapper) : Presenter<PostListView>() {

    override fun attachView(view: PostListView) {
        super.attachView(view)
        view.loading(true)
        get()
    }

    override fun detachView() {
        view?.loading(false)
        super.detachView()
    }

    fun get(refresh: Boolean = false) {
        addDisposable(useCase.get(refresh)
                .subscribeOn(Schedulers.io())
                .map { mapper.map(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { view?.loading(false) }
                .subscribe({ view?.add(it) }, { view?.error() }))
    }
}
