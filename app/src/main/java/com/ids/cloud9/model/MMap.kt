package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class MMap(
    @SerializedName("collapse_key")
    val collapseKey: String,
    @SerializedName("com.google.firebase.iid.WakeLockHolder.wakefulintent")
    val comGoogleFirebaseIidWakeLockHolderWakefulintent: Boolean,
    @SerializedName("from")
    val from: String,
    @SerializedName("gcm.n.dnp")
    val gcmNDnp: String,
    @SerializedName("gcm.notification.body")
    val gcmNotificationBody: String,
    @SerializedName("gcm.notification.e")
    val gcmNotificationE: String,
    @SerializedName("gcm.notification.title")
    val gcmNotificationTitle: String,
    @SerializedName("google.c.a.e")
    val googleCAE: String,
    @SerializedName("google.c.sender.id")
    val googleCSenderId: String,
    @SerializedName("google.delivered_priority")
    val googleDeliveredPriority: String,
    @SerializedName("google.message_id")
    val googleMessageId: String,
    @SerializedName("google.original_priority")
    val googleOriginalPriority: String,
    @SerializedName("google.sent_time")
    val googleSentTime: Long,
    @SerializedName("google.ttl")
    val googleTtl: Int,
    @SerializedName("notification_foreground")
    val notificationForeground: String,
    @SerializedName("visitId")
    val visitId: String
)