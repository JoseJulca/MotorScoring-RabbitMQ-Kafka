namespace MotorScoring.Web.Models.Responses;

public sealed record EvaluacionScoringResponse(
    long IdEvaluacion,
    long IdSolicitud,
    int PuntajeTotal,
    string Resultado,
    string Estado,
    string VersionModelo,
    DateTime FechaEvaluacion,
    IReadOnlyList<ResultadoFactorResponse> Factores);
