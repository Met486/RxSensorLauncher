package com.example.rxsensorlauncher

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem

class SensorService : JobScheduler() {
    override fun schedule(jobInfo: JobInfo): Int {
        return 0
    }

    override fun enqueue(jobInfo: JobInfo, jobWorkItem: JobWorkItem): Int {
        return 0
    }

    override fun cancel(i: Int) {}
    override fun cancelAll() {}
    override fun getAllPendingJobs(): List<JobInfo> {
        return null
    }

    override fun getPendingJob(i: Int): JobInfo? {
        return null
    }
}