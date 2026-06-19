package com.moviles.jobmatch.data.remote

import com.moviles.jobmatch.core.AppConstants
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.CompanySummary
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.data.remote.model.LoginRequest
import com.moviles.jobmatch.data.remote.model.LoginResponse
import com.moviles.jobmatch.data.remote.model.RegisterResponse
import com.moviles.jobmatch.data.remote.model.RegisterCompanyRequest
import com.moviles.jobmatch.data.remote.model.RegisterStudentRequest
import com.moviles.jobmatch.data.remote.model.CreateJobRequest
import com.moviles.jobmatch.data.remote.model.CreateJobResponse
import com.moviles.jobmatch.data.remote.model.ApplicationResponse
import com.moviles.jobmatch.data.remote.model.CreateApplicationRequest
import com.moviles.jobmatch.data.remote.model.CreateApplicationResponse
import com.moviles.jobmatch.data.remote.model.UpdateApplicationRequest
import com.moviles.jobmatch.data.remote.model.UpdateApplicationResponse
import com.moviles.jobmatch.data.remote.model.UpdateJobRequest
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.ContractAcceptResponse
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import com.moviles.jobmatch.data.remote.model.DeleteUserRequest
import com.moviles.jobmatch.data.remote.model.PaymentResponse
import com.moviles.jobmatch.data.remote.model.UpdateAvailabilityRequest
import com.moviles.jobmatch.data.remote.model.CreatePaymentRequest
import com.moviles.jobmatch.data.remote.model.CreatePaymentResponse
import com.moviles.jobmatch.data.remote.model.RegisterFcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(AppConstants.Api.Paths.AUTH_REGISTER_STUDENT)
    suspend fun registerStudent(@Body request: RegisterStudentRequest): Response<RegisterResponse>

    @POST(AppConstants.Api.Paths.AUTH_REGISTER_COMPANY)
    suspend fun registerCompany(@Body request: RegisterCompanyRequest): Response<RegisterResponse>

    @GET("companies/{companyId}")
    suspend fun getCompanyProfile(@Path("companyId") companyId: String): Response<Company>

    @GET("companies")
    suspend fun getAllCompanies(): List<CompanySummary>

    @GET("jobs")
    suspend fun getAllJobs(): Response<List<Job>>

    @GET("jobs/recommended")
    suspend fun getRecommendedJobs(): Response<List<Job>>

    @POST("jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<CreateJobResponse>
  
    @GET("jobs/{jobId}")
    suspend fun getJobById(@Path("jobId") jobId: Int): Response<JobDetailResponse>

    @PUT("jobs/{jobId}")
    suspend fun updateJob(
        @Path("jobId") jobId: Int,
        @Body request: UpdateJobRequest
    ): Response<JobDetailResponse>

    @POST("applications")
    suspend fun applyToJob(@Body request: CreateApplicationRequest): Response<CreateApplicationResponse>

    @GET("jobs/{jobId}/applications")
    suspend fun getApplicationsByJob(@Path("jobId") jobId: Int): Response<List<ApplicationResponse>>

    @PUT("applications/{applicationId}")
    suspend fun updateApplicationStatus(
        @Path("applicationId") applicationId: Int,
        @Body request: UpdateApplicationRequest
    ): Response<UpdateApplicationResponse>

    @GET(AppConstants.Api.Paths.STUDENT_PROFILE_PATH)
    suspend fun getStudentProfile(
        @Path("studentId") studentId: String
    ): Response<StudentProfileResponse>

    @GET("students/{studentId}/availability")
    suspend fun getAvailability(
        @Path("studentId") studentId: String
    ): Response<AvailabilityResponse>

    @PUT("students/{studentId}/availability")
    suspend fun updateAvailability(
        @Path("studentId") studentId: String,
        @Body body: UpdateAvailabilityRequest
    ): Response<Unit>

    @GET("contracts/student")
    suspend fun getStudentContracts(): Response<List<ContractListResponse>>

    @GET("contracts")
    suspend fun getCompanyContracts(
        @Query("status") status: String? = null
    ): Response<List<ContractListResponse>>

    @GET("contracts/{contractId}")
    suspend fun getContractById(@Path("contractId") contractId: Int): Response<ContractDetailResponse>

    @PUT("contracts/{contractId}/accept")
    suspend fun acceptContract(@Path("contractId") contractId: Int): Response<ContractAcceptResponse>

    @GET("payments")
    suspend fun getPaymentHistory(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<PaymentResponse>>

    @HTTP(method = "DELETE", path = "users/{userId}", hasBody = true)
    suspend fun deleteUser(
        @Path("userId") userId: String,
        @Body request: DeleteUserRequest
    ): Response<Unit>

    @POST("payments")
    suspend fun createPayment(
        @Body request: CreatePaymentRequest
    ): Response<CreatePaymentResponse>

    @POST("notifications/token")
    suspend fun registerFcmToken(@Body request: RegisterFcmTokenRequest): Response<Unit>

    @DELETE("notifications/token/{token}")
    suspend fun deleteFcmToken(@Path("token") token: String): Response<Unit>
}