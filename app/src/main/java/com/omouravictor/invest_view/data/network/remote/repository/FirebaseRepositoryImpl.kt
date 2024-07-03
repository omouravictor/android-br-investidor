package com.omouravictor.invest_view.data.network.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import kotlinx.coroutines.tasks.await

class FirebaseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    override suspend fun saveAsset(assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return try {
            // TODO: altenticar antes e não aqui
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado.")

            firestore.collection("users").document(userId)
                .collection("assets").document(assetUiModel.symbol)
                .set(assetUiModel).await()

            Result.success(assetUiModel)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAsset(assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado.")

            firestore.collection("users").document(userId)
                .collection("assets").document(assetUiModel.symbol)
                .delete().await()

            Result.success(assetUiModel)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAssetsList(): Result<List<AssetUiModel>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado.")

            val assetsList = firestore.collection("users").document(userId)
                .collection("assets").get().await()
                .toObjects(AssetUiModel::class.java)

            Result.success(assetsList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}