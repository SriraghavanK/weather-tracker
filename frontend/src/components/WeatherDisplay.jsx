import { Thermometer, Cloud, Wind, MapPin, AlertTriangle, Droplets, Gauge } from 'lucide-react'

function WeatherDisplay({ weather }) {
  const getWeatherIcon = (condition) => {
    const lowerCondition = condition.toLowerCase()
    if (lowerCondition.includes('clear') || lowerCondition.includes('sunny')) {
      return '☀️'
    } else if (lowerCondition.includes('cloud')) {
      return '☁️'
    } else if (lowerCondition.includes('rain')) {
      return '🌧️'
    } else if (lowerCondition.includes('snow')) {
      return '❄️'
    } else if (lowerCondition.includes('storm') || lowerCondition.includes('thunder')) {
      return '⛈️'
    } else if (lowerCondition.includes('fog') || lowerCondition.includes('mist')) {
      return '🌫️'
    }
    return '🌤️'
  }

  const getGradientForCondition = (condition) => {
    const lowerCondition = condition.toLowerCase()
    if (lowerCondition.includes('clear') || lowerCondition.includes('sunny')) {
      return 'from-orange-500 to-yellow-500'
    } else if (lowerCondition.includes('rain')) {
      return 'from-blue-600 to-cyan-500'
    } else if (lowerCondition.includes('cloud')) {
      return 'from-gray-500 to-slate-500'
    } else if (lowerCondition.includes('snow')) {
      return 'from-blue-300 to-white'
    } else if (lowerCondition.includes('storm') || lowerCondition.includes('thunder')) {
      return 'from-purple-600 to-blue-600'
    }
    return 'from-blue-500 to-purple-500'
  }

  return (
    <div className="mt-12 animate-slide-up" style={{ animationDelay: '0.2s' }}>
      <div className="relative">
        <div className="absolute -inset-1 bg-gradient-to-r from-blue-500 to-purple-500 rounded-3xl blur opacity-20"></div>
        <div className="relative bg-slate-800/50 backdrop-blur-xl border border-white/10 rounded-3xl p-6 sm:p-10">
          {/* Location Header */}
          <div className="flex flex-col sm:flex-row items-center sm:justify-between gap-6 mb-10">
            <div className="text-center sm:text-left">
              <div className="flex items-center justify-center sm:justify-start gap-3 mb-2">
                <MapPin className="w-6 h-6 text-blue-400" />
                <h2 className="text-3xl sm:text-4xl font-bold text-white">
                  {weather.cityName}
                </h2>
              </div>
              <p className="text-slate-400 text-sm">
                {weather.latitude.toFixed(4)}°N, {weather.longitude.toFixed(4)}°E
              </p>
            </div>
            <div className="text-7xl sm:text-8xl animate-float">
              {getWeatherIcon(weather.condition)}
            </div>
          </div>
          
          {/* Weather Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-6">
            {/* Temperature Card */}
            <div className="group relative">
              <div className="absolute -inset-0.5 bg-gradient-to-r from-orange-500 to-red-500 rounded-2xl blur opacity-30 group-hover:opacity-50 transition duration-500"></div>
              <div className="relative bg-slate-900/50 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-orange-500/20 rounded-lg">
                    <Thermometer className="w-5 h-5 text-orange-400" />
                  </div>
                  <p className="text-slate-400 text-sm font-medium">Temperature</p>
                </div>
                <p className="text-4xl sm:text-5xl font-bold text-white">
                  {weather.temperature.toFixed(1)}°
                  <span className="text-xl sm:text-2xl font-normal text-slate-400 ml-1">C</span>
                </p>
              </div>
            </div>
            
            {/* Condition Card */}
            <div className="group relative">
              <div className="absolute -inset-0.5 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl blur opacity-30 group-hover:opacity-50 transition duration-500"></div>
              <div className="relative bg-slate-900/50 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-blue-500/20 rounded-lg">
                    <Cloud className="w-5 h-5 text-blue-400" />
                  </div>
                  <p className="text-slate-400 text-sm font-medium">Condition</p>
                </div>
                <p className="text-xl sm:text-2xl font-semibold text-white">
                  {weather.condition}
                </p>
              </div>
            </div>
            
            {/* Wind Speed Card */}
            <div className="group relative">
              <div className="absolute -inset-0.5 bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl blur opacity-30 group-hover:opacity-50 transition duration-500"></div>
              <div className="relative bg-slate-900/50 backdrop-blur-sm rounded-2xl p-6 border border-white/10">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-purple-500/20 rounded-lg">
                    <Wind className="w-5 h-5 text-purple-400" />
                  </div>
                  <p className="text-slate-400 text-sm font-medium">Wind Speed</p>
                </div>
                <p className="text-4xl sm:text-5xl font-bold text-white">
                  {weather.windSpeed.toFixed(1)}
                  <span className="text-xl sm:text-2xl font-normal text-slate-400 ml-1">km/h</span>
                </p>
              </div>
            </div>
          </div>
          
          {/* Alerts Section */}
          {weather.alerts && weather.alerts.length > 0 && (
            <div className="mt-8 p-6 bg-gradient-to-r from-amber-500/10 to-orange-500/10 backdrop-blur-sm border border-amber-500/20 rounded-2xl">
              <div className="flex items-start gap-4">
                <div className="flex-shrink-0 w-12 h-12 bg-amber-500/20 rounded-full flex items-center justify-center animate-pulse">
                  <AlertTriangle className="w-6 h-6 text-amber-400" />
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-amber-400 text-lg mb-3 flex items-center gap-2">
                    <span>Weather Alerts</span>
                    <span className="px-2 py-0.5 bg-amber-500/20 rounded-full text-xs text-amber-300">
                      {weather.alerts.length}
                    </span>
                  </h3>
                  <ul className="space-y-2">
                    {weather.alerts.map((alert, index) => (
                      <li key={index} className="text-amber-200/80 text-sm flex items-start gap-2">
                        <span className="w-1.5 h-1.5 bg-amber-400 rounded-full mt-1.5 flex-shrink-0"></span>
                        {alert}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default WeatherDisplay
