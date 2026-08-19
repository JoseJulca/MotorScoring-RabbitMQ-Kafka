using System.Net.Http.Json;
using MotorScoring.Web.Models.Requests;
using MotorScoring.Web.Models.Responses;

namespace MotorScoring.Web.Services;

public sealed class SolicitudCreditoApiService : ISolicitudCreditoApiService
{
    private readonly HttpClient _httpClient;

    public SolicitudCreditoApiService(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<RegistrarSolicitudResponse> RegistrarAsync(
        RegistrarSolicitudRequest request,
        CancellationToken cancellationToken = default)
    {
        using var response = await _httpClient.PostAsJsonAsync(
            "/api/solicitudes-credito",
            request,
            cancellationToken);

        if (!response.IsSuccessStatusCode)
        {
            throw await BuildExceptionAsync(response, cancellationToken);
        }

        return await response.Content.ReadFromJsonAsync<RegistrarSolicitudResponse>(
                   cancellationToken: cancellationToken)
               ?? throw new ApiException(
                   500,
                   "La API Java no devolvió una respuesta válida al registrar la solicitud.");
    }

    public async Task<EvaluacionScoringResponse> EvaluarAsync(
        long idSolicitud,
        CancellationToken cancellationToken = default)
    {
        using var response = await _httpClient.PostAsync(
            $"/api/solicitudes-credito/{idSolicitud}/evaluar",
            content: null,
            cancellationToken);

        if (!response.IsSuccessStatusCode)
        {
            throw await BuildExceptionAsync(response, cancellationToken);
        }

        return await response.Content.ReadFromJsonAsync<EvaluacionScoringResponse>(
                   cancellationToken: cancellationToken)
               ?? throw new ApiException(
                   500,
                   "La API Java no devolvió una respuesta válida al evaluar la solicitud.");
    }

    private static async Task<ApiException> BuildExceptionAsync(
        HttpResponseMessage response,
        CancellationToken cancellationToken)
    {
        try
        {
            var error = await response.Content.ReadFromJsonAsync<ApiErrorResponse>(
                cancellationToken: cancellationToken);

            if (error is not null && !string.IsNullOrWhiteSpace(error.Message))
            {
                return new ApiException((int)response.StatusCode, error.Message);
            }
        }
        catch
        {
            // El backend Java puede devolver otro formato de error.
        }

        string? raw = null;
        try
        {
            raw = await response.Content.ReadAsStringAsync(cancellationToken);
        }
        catch
        {
            // Se usa el mensaje genérico.
        }

        var message = !string.IsNullOrWhiteSpace(raw)
            ? raw
            : $"La API Java respondió con HTTP {(int)response.StatusCode} ({response.ReasonPhrase}).";

        return new ApiException((int)response.StatusCode, message);
    }
}
