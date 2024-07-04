package com.omouravictor.invest_view.data.network.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import kotlinx.coroutines.tasks.await

class FirebaseRepositoryImpl(private val firestore: FirebaseFirestore) : FirebaseRepository {

    override suspend fun getAssetList(userId: String): Result<List<AssetUiModel>> {
        return try {
            val assetsList = firestore.collection("users").document(userId)
                .collection("assets").get().await()
                .toObjects(AssetUiModel::class.java)

            Result.success(assetsList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return try {
            firestore.collection("users").document(userId)
                .collection("assets").document(assetUiModel.symbol)
                .set(assetUiModel).await()

            Result.success(assetUiModel)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return try {
            firestore.collection("users").document(userId)
                .collection("assets").document(assetUiModel.symbol)
                .delete().await()

            Result.success(assetUiModel)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}