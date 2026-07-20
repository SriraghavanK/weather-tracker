import { useState } from 'react'
import { Search, MapPin, ArrowRight } from 'lucide-react'

function WeatherSearch({ onSearch, loading }) {
  const [location, setLocation] = useState('')
  const [isFocused, setIsFocused] = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault()
    if (location.trim()) {
      onSearch(location.trim())
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-8 animate-slide-up" style={{ animationDelay: '0.1s' }}>
      <div className={`relative group transition-all duration-500 ${isFocused ? 'scale-[1.02]' : 'scale-100'}`}>
        <div className="absolute -inset-1 bg-gradient-to-r from-blue-500 to-purple-500 rounded-2xl blur opacity-20 group-hover:opacity-40 transition duration-500"></div>
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-4 sm:pl-6 flex items-center pointer-events-none">
            <Search className={`h-5 w-5 sm:h-6 sm:w-6 transition-colors duration-300 ${isFocused ? 'text-blue-400' : 'text-slate-400'}`} />
          </div>
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
            placeholder="Search city name or coordinates..."
            className="w-full pl-12 sm:pl-16 pr-32 sm:pr-40 py-4 sm:py-5 bg-slate-800/50 backdrop-blur-xl border border-white/10 rounded-2xl focus:outline-none focus:border-blue-500/50 text-white placeholder-slate-400 text-base sm:text-lg shadow-2xl transition-all duration-300"
            disabled={loading}
          />
          <button
            type="submit"
            disabled={loading || !location.trim()}
            className="absolute right-2 sm:right-3 top-1/2 -translate-y-1/2 px-4 sm:px-6 py-2.5 sm:py-3 bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white rounded-xl font-medium focus:outline-none focus:ring-2 focus:ring-blue-500/50 disabled:from-slate-600 disabled:to-slate-700 disabled:cursor-not-allowed transition-all duration-300 shadow-lg hover:shadow-blue-500/25 transform hover:scale-105 active:scale-95 flex items-center gap-2 text-sm sm:text-base"
          >
            {loading ? (
              <svg className="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : (
              <>
                <span className="hidden sm:inline">Search</span>
                <ArrowRight className="w-4 h-4 sm:hidden" />
              </>
            )}
          </button>
        </div>
      </div>
      <div className="mt-4 flex items-center justify-center gap-2 text-sm text-slate-400">
        <MapPin className="w-4 h-4 text-blue-400" />
        <span className="hidden sm:inline">Try: "bangalore", "new york", "12.97, 77.59"</span>
        <span className="sm:hidden">Try: "bangalore", "12.97, 77.59"</span>
      </div>
    </form>
  )
}

export default WeatherSearch
