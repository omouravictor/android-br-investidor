package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.di.model.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.UserUiModel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_ASSETS = "assets"
    }

    override suspend fun getUser(userId: String): Result<UserUiModel?> {
        return withContext(dispatchers.io) {
            try {
                val userUiModel = firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .get()
                    .await()
                    .toObject(UserUiModel::class.java)

                Result.success(userUiModel)

            } catch (e: Exception) {
                Log.e("GetUser", "UserId: $userId", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun saveUser(userUiModel: UserUiModel): Result<UserUiModel> {
        return withContext(dispatchers.io) {
            try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userUiModel.uid)
                    .set(userUiModel)
                    .await()

                Result.success(userUiModel)

            } catch (e: Exception) {
                Log.e("SaveUser", "UserId: ${userUiModel.uid}", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getAssetList(userId: String): Result<List<AssetUiModel>> {
        return withContext(dispatchers.io) {
            try {
                val assetsList = firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_ASSETS)
                    .get()
                    .await()
                    .toObjects(AssetUiModel::class.java)

                Result.success(assetsList)

            } catch (e: Exception) {
                Log.e("GetAssetList", "UserId: $userId", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return withContext(dispatchers.io) {
            try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_ASSETS)
                    .document(assetUiModel.symbol)
                    .set(assetUiModel)
                    .await()

                Result.success(assetUiModel)

            } catch (e: Exception) {
                Log.e("SaveAsset", "UserId: $userId | Asset: ${assetUiModel.symbol}", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel> {
        return withContext(dispatchers.io) {
            try {
                firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_ASSETS)
                    .document(assetUiModel.symbol)
                    .delete()
                    .await()

                Result.success(assetUiModel)

            } catch (e: Exception) {
                Log.e("DeleteAsset", "UserId: $userId | Asset: ${assetUiModel.symbol}", e)
                Result.failure(e)
            }
        }
    }

}