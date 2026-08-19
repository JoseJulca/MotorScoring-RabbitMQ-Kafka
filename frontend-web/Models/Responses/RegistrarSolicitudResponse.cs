namespace MotorScoring.Web.Models.Responses;

public sealed record RegistrarSolicitudResponse(
    long IdSolicitud,
    long IdSolicitante,
    string IdentificadorExterno,
    string CodigoProducto,
    decimal MontoSolicitado,
    int PlazoSolicitado,
    string Moneda,
    string Estado,
    DateTime FechaRegistro);
