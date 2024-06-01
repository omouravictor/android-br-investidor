package com.omouravictor.invest_view.data.network.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

class FirebaseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    override fun saveAsset(assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado.")

            firestore.collection("users").document(userId)
                .collection("assets").document(assetUiModel.symbol)
                .set(assetUiModel)
                .addOnFailureListener { throw it }

            Result.success(assetUiModel)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}