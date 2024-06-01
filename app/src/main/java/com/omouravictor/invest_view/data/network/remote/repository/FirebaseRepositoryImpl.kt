package com.omouravictor.invest_view.data.network.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    override fun saveAsset(assetUiModel: AssetUiModel): Flow<NetworkState<AssetUiModel>> {
        return flow {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado.")

                firestore.collection("users").document(userId)
                    .collection("assets").document(assetUiModel.symbol)
                    .set(assetUiModel)
                    .addOnFailureListener { throw it }

                emit(NetworkState.Success(assetUiModel))

            } catch (e: Exception) {
                emit(NetworkState.Error(e))
            }
        }
    }

}