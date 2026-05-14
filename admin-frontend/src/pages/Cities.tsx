import { useEffect, useState } from 'react'
import { getCities, createCity, updateCity, deleteCity, City } from '../api/client'

export default function Cities() {
  const [cities, setCities] = useState<City[]>([])
  const [editCity, setEditCity] = useState<City | null>(null)
  const [editName, setEditName] = useState('')
  const [showDialog, setShowDialog] = useState(false)

  useEffect(() => { getCities().then(setCities) }, [])

  const openCreate = () => {
    setEditCity(null)
    setEditName('')
    setShowDialog(true)
  }

  const openEdit = (city: City) => {
    setEditCity(city)
    setEditName(city.name)
    setShowDialog(true)
  }

  const handleSave = async () => {
    if (!editName.trim()) return
    if (editCity) {
      await updateCity(editCity.id, editName)
    } else {
      await createCity(editName)
    }
    setShowDialog(false)
    getCities().then(setCities)
  }

  const handleDelete = async (id: string, name: string) => {
    if (!confirm(`Delete city '${name}'?`)) return
    await deleteCity(id)
    getCities().then(setCities)
  }

  return (
    <div>
      <div className="page-header">
        <h1>Cities</h1>
        <button className="btn btn-primary" style={{ width: 'auto' }} onClick={openCreate}>+ Add City</button>
      </div>

      <table>
        <thead>
          <tr><th>Name</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {cities.map(c => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>
                <button className="btn btn-sm btn-primary" onClick={() => openEdit(c)} style={{ marginRight: '0.5rem' }}>Edit</button>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(c.id, c.name)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showDialog && (
        <div className="dialog-overlay" onClick={() => setShowDialog(false)}>
          <div className="dialog" onClick={e => e.stopPropagation()}>
            <h3>{editCity ? 'Edit City' : 'Add City'}</h3>
            <div className="form-group">
              <label>City Name</label>
              <input value={editName} onChange={e => setEditName(e.target.value)} autoFocus />
            </div>
            <div className="dialog-actions">
              <button className="btn btn-sm" onClick={() => setShowDialog(false)}>Cancel</button>
              <button className="btn btn-sm btn-primary" onClick={handleSave}>Save</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
