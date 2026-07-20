import { useState } from 'react'
import { Cloud, AlertCircle, Loader2, Sparkles } from 'lucide-react'
import WeatherSearch from './components/WeatherSearch'
import WeatherDisplay from './components/WeatherDisplay'
import './App.css'

function App() {
  const [weatherData, setWeatherData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSearch = async (location) => {
    setLoading(true)
    setError(null)
    setWeatherData(null)

    try {
      const response = await fetch(`http://localhost:8080/api/weather?location=${encodeURIComponent(location)}`)
      if (!response.ok) {
        throw new Error('Location not found')
      }
      const data = await response.json()
      setWeatherData(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-float"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-float" style={{ animationDelay: '2s' }}></div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-pink-500 rounded-full mix-blend-multiply filter blur-3xl opacity-10 animate-float" style={{ animationDelay: '4s' }}></div>
      </div>

      <div className="relative z-10 container mx-auto px-4 py-12 sm:py-16 max-w-5xl">
        {/* Header */}
        <div className="text-center mb-12 sm:mb-16 animate-slide-up">
          <div className="inline-flex items-center justify-center gap-3 mb-6">
            <div className="relative">
              <Cloud className="w-12 h-12 sm:w-16 sm:h-16 text-blue-400" />
              <Sparkles className="absolute -top-1 -right-1 w-5 h-5 text-yellow-400 animate-pulse" />
            </div>
            <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
              Weather Tracker
            </h1>
          </div>
          <p className="text-slate-300 text-lg sm:text-xl max-w-2xl mx-auto leading-relaxed">
            Experience real-time weather intelligence with beautiful visualizations
          </p>
        </div>
        
        <WeatherSearch onSearch={handleSearch} loading={loading} />
        
        {loading && (
          <div className="mt-12 flex flex-col items-center justify-center p-12 bg-white/5 backdrop-blur-xl rounded-3xl border border-white/10 animate-scale-in">
            <div className="relative">
              <div className="absolute inset-0 bg-blue-500/20 rounded-full blur-xl animate-pulse"></div>
              <Loader2 className="w-16 h-16 text-blue-400 animate-spin relative" />
            </div>
            <p className="text-slate-300 text-lg mt-6 font-medium">Fetching weather data...</p>
            <p className="text-slate-500 text-sm mt-2">Analyzing atmospheric conditions</p>
          </div>
        )}
        
        {error && (
          <div className="mt-12 p-8 bg-red-500/10 backdrop-blur-xl border border-red-500/20 rounded-3xl animate-slide-up">
            <div className="flex items-start gap-4">
              <div className="flex-shrink-0 w-12 h-12 bg-red-500/20 rounded-full flex items-center justify-center">
                <AlertCircle className="w-6 h-6 text-red-400" />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-red-400 text-lg mb-2">Unable to fetch weather data</h3>
                <p className="text-red-300/80">{error}</p>
              </div>
            </div>
          </div>
        )}
        
        {weatherData && <WeatherDisplay weather={weatherData} />}
      </div>
    </div>
  )
}

export default App
