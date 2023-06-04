package com.where2meet.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.where2meet.core.data.remote.api.ApiService
import com.where2meet.core.data.remote.json.group.MiniGroupJson
import com.where2meet.core.data.toModel
import com.where2meet.core.domain.model.MiniGroup
import retrofit2.HttpException
import java.io.IOException

class GroupPagingSource(
    private val api: ApiService,
    private val token: String,
) : PagingSource<Int, MiniGroup>() {
    override fun getRefreshKey(state: PagingState<Int, MiniGroup>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MiniGroup> {
        try {
            val nextPage = params.key ?: 1
            val response =
                api.fetchGroups(token = token, page = nextPage, size = params.loadSize)
            val pagedResponse = response.body()

            val data: List<MiniGroup>? = pagedResponse?.data?.groups?.map(MiniGroupJson::toModel)

            return LoadResult.Page(
                data = data.orEmpty(),
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (data.isNullOrEmpty()) null else nextPage + 1
            )
        } catch (ex: HttpException) {
            throw IOException(ex)
        } catch (ex: IOException) {
            return LoadResult.Error(ex)
        }
    }
}
