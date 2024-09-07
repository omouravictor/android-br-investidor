package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.di.model.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    override suspend fun getAssetList(userId: String): Result<List<AssetUiModel>> {
        return withContext(dispatchers.io) {
            try {
                val assetsList = firestore.collection("users").document(userId)
                    .collection("assets").get().await()
                    .toObjects(AssetUiModel::class.java)

                Result.success(assetsList)

            } catch (e: Exception) {
                Log.e("GetAssetList", "User $userId", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return withContext(dispatchers.io) {
            try {
                firestore.collection("users").document(userId)
                    .collection("assets").document(assetUiModel.symbol)
                    .set(assetUiModel).await()

                Result.success(assetUiModel)

            } catch (e: Exception) {
                Log.e("SaveAsset", "User $userId | Asset ${assetUiModel.symbol}", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return withContext(dispatchers.io) {
            try {
                firestore.collection("users").document(userId)
                    .collection("assets").document(assetUiModel.symbol)
                    .delete().await()

                Result.success(assetUiModel)

            } catch (e: Exception) {
                Log.e("DeleteAsset", "User $userId | Asset ${assetUiModel.symbol}", e)
                Result.failure(e)
            }
        }
    }

}