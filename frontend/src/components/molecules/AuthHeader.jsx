export const AuthHeader = ({ title, subtitle, showLogo = true }) => {
  return (
    <div className="text-center mb-8">
      {showLogo && (
        <div className="flex justify-center mb-6">
          <img
            src="/nexora-logo.svg"
            alt="Nexora"
            className="h-12 w-auto"
            onError={(e) => {
              e.target.style.display = 'none'
            }}
          />
        </div>
      )}
      <h1 className="text-2xl font-semibold text-primary mb-2">{title}</h1>
      {subtitle && <p className="text-sm text-secondary">{subtitle}</p>}
    </div>
  )
}
